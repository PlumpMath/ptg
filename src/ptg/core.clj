(ns ptg.core
  (:require [mikera.image.core :as img]
            [seesaw.core :as see])
  (:import java.awt.image.BufferedImage))


(defn- line-x [img y x0 x1]
  (doseq [x (range x0 x1)]
    (.setRGB img x y 0)))


(defn- line-y [img x y0 y1]
  (doseq [y (range y0 y1)]
    (.setRGB img x y 0)))


(defn dogrid [file-ob]
  (let [img (img/load-image file-ob)
        image-width (.getWidth img)
        image-height (.getHeight img)
        support-width 11
        support-height 5
        support-grid-spacing 1

        ppi (min (/ image-width support-width)
                 (/ image-height support-height))

        pixels-per-square (* ppi support-grid-spacing)

        lines-across (/ (* support-width ppi) pixels-per-square)
        lines-down (/ (* support-height ppi) pixels-per-square)

        ;; Only generate grid covering canvas/support area...
        pixels-horiz-on-support (* ppi support-width)
        pixels-vert-on-support (* ppi support-height)
        margin-side (/ (- image-width pixels-horiz-on-support) 2)
        margin-top (/ (- image-height pixels-vert-on-support) 2)

        img-scroll (see/scrollable (see/label :icon img))
        input-form (see/horizontal-panel
                    :items [(see/label :text " Support size: ")
                            (see/text :text (str support-width))
                            (see/label :text " inches/cm wide by ")
                            (see/text (str support-height))
                            (see/label :text " inches/cm high. Square size: ")
                            (see/text (str support-grid-spacing))
                            (see/label :text " inch/cm. ")])
        contents (see/vertical-panel :items [input-form
                                             img-scroll])
        f (see/frame :content contents
                     :title (.getFile file-ob))]
    (doseq [x (map #(+ margin-side (* pixels-per-square %))
                   (range lines-across))]
      (line-y img x margin-top (+ margin-top pixels-vert-on-support)))
    (line-y img
            (dec (+ margin-side pixels-horiz-on-support))
            margin-top (+ margin-top pixels-vert-on-support))
    (doseq [y (map #(+ margin-top (* pixels-per-square %))
                   (range lines-down))]
      (line-x img y margin-side (+ margin-side pixels-horiz-on-support)))
    (line-x img
            (dec (+ margin-top pixels-vert-on-support))
            margin-side (+ margin-side pixels-horiz-on-support))
    (see/pack! f)
    (see/show! f)))


(dogrid (clojure.java.io/resource "img.jpg"))
