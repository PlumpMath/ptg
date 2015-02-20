(ns ptg.core
  (:gen-class)
  (:require [mikera.image.core :as img]
            [ptg.grid :refer [gridlines]]
            [seesaw.core :as see])
  (:import java.awt.image.BufferedImage))


(def default-support-width 11)
(def default-support-height 5)
(def default-support-grid-spacing 1)


(defn- line-x [img y x0 x1]
  (doseq [x (range x0 x1)]
    (.setRGB img x y 0)))


(defn- line-y [img x y0 y1]
  (doseq [y (range y0 y1)]
    (.setRGB img x y 0)))


(defn -main [& [filename _]]
  (when-not filename
    (println "Usage: ptg <filename>")
    (System/exit 0))
  (let [f (see/frame)
        width-txt (see/text (str default-support-width))
        height-txt (see/text (str default-support-height))
        grid-txt (see/text (str default-support-grid-spacing))
        lbl (see/label)

        input-form
        (see/horizontal-panel
         :items
         [(see/label :text " Support size: ") width-txt
          (see/label :text " inches/cm wide by ") height-txt
          (see/label :text " inches/cm high. Square size: ") grid-txt
          (see/label :text " inch/cm. ")])

        draw-image-with-lines
        (fn [support-width
             support-height
             support-grid-spacing]
          (let [img (img/load-image filename)
                {:keys [xlines ylines]}
                (gridlines support-width support-height support-grid-spacing
                           (.getWidth img) (.getHeight img))
                contents (see/vertical-panel :items [input-form
                                                     (see/scrollable lbl)])]
            (doseq [[y x0 x1] xlines] (line-x img y x0 x1))
            (doseq [[x y0 y1] ylines] (line-y img x y0 y1))
            (see/config! f :content contents)
            (see/config! lbl :icon img)
            (see/pack! f)
            (see/show! f)
            (see/request-focus! width-txt)))

        callback
        (fn [e]
          (if (= 10 (.getKeyCode e))
            (let [width (Double/parseDouble (see/config width-txt :text))
                  height (Double/parseDouble (see/config height-txt :text))
                  grid-size (Double/parseDouble (see/config grid-txt :text))]
              (draw-image-with-lines width height grid-size))))]

    (see/config! f :title filename)
    (see/listen width-txt :key-pressed callback)
    (see/listen height-txt :key-pressed callback)
    (see/listen grid-txt :key-pressed callback)

    (see/native!)
    (draw-image-with-lines 12 10 3)))
