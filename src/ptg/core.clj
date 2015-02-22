(ns ptg.core
  (:gen-class)
  (:require [mikera.image.core :as img]
            [ptg.grid :refer [gridlines]]
            [seesaw.core :as see])
  (:import java.awt.image.BufferedImage))


(def default-support-width 11)
(def default-support-height 5)
(def default-support-grid-spacing 1)


(defn- in-bounds [img x y]
  (let [w (.getWidth img)
        h (.getHeight img)]
    (and (< 0 x w)
         (< 0 y h))))


(defn- line-x [img y x0 x1]
  (doseq [x (range x0 x1)]
    (when (in-bounds img x y)
      (.setRGB img x y 0))))


(defn- line-y [img x y0 y1]
  (doseq [y (range y0 y1)]
    (when (in-bounds img x y)
      (.setRGB img x y 0))))


(defn -main [& [filename _]]
  (when-not filename
    (println "Usage: ptg <filename>")
    (System/exit 0))
  (let [f (see/frame)
        width-txt (see/text (str default-support-width))
        height-txt (see/text (str default-support-height))
        grid-txt (see/text (str default-support-grid-spacing))
        offs-x-txt (see/text "0")
        offs-y-txt (see/text "0")
        lbl (see/label)

        input-form
        (see/horizontal-panel
         :items
         [(see/label :text " Support size: ") width-txt
          (see/label :text " inches/cm wide by ") height-txt
          (see/label :text " inches/cm high. Square size: ") grid-txt
          (see/label :text " inch/cm. Offset: ") offs-x-txt offs-y-txt])

        draw-image-with-lines
        (fn [support-width
             support-height
             support-grid-spacing
             offs-x offs-y]
          (let [img (img/load-image filename)
                {:keys [xlines ylines]}
                (gridlines support-width support-height support-grid-spacing
                           (.getWidth img) (.getHeight img))
                contents (see/vertical-panel :items [input-form
                                                     (see/scrollable lbl)])]
            (doseq [[y x0 x1] xlines]
              (line-x img (+ offs-y y) (+ offs-x x0) (+ offs-x x1)))
            (doseq [[x y0 y1] ylines]
              (line-y img (+ offs-x x) (+ offs-y y0) (+ offs-y y1)))
            (see/config! f :content contents)
            (see/config! lbl :icon img)
            (see/pack! f)
            (see/show! f)
            (see/request-focus! width-txt)))

        get-field-num #(Double/parseDouble (see/config % :text))

        callback
        (fn [e]
          (if (= 10 (.getKeyCode e))
            (let [width (get-field-num width-txt)
                  height (get-field-num height-txt)
                  grid-size (get-field-num grid-txt)
                  offs-x (get-field-num offs-x-txt)
                  offs-y (get-field-num offs-y-txt)]
              (draw-image-with-lines width height grid-size offs-x offs-y))))]

    (see/config! f :title filename)
    (see/listen width-txt :key-pressed callback)
    (see/listen height-txt :key-pressed callback)
    (see/listen grid-txt :key-pressed callback)
    (see/listen offs-x-txt :key-pressed callback)
    (see/listen offs-y-txt :key-pressed callback)

    (see/native!)
    (draw-image-with-lines default-support-width
                           default-support-height
                           default-support-grid-spacing
                           0 0)))


(-main (clojure.java.io/resource "img.jpg"))
