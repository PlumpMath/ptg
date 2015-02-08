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
        support-height 8.5
        support-grid-spacing 0.5
        lines-across (int (/ support-width support-grid-spacing))
        lines-down (int (/ support-height support-grid-spacing))
        pixels-between-lines (int (/ image-width lines-across))
        img-scroll (see/scrollable (see/label :icon img))
        input-form (see/horizontal-panel
                    :items [(see/label :text " Support size: ")
                            (see/text :text (str support-width))
                            (see/label :text " inches wide by ")
                            (see/text (str support-height))
                            (see/label :text " inches high. Square size: ")
                            (see/text (str support-grid-spacing))
                            (see/label :text " inch ")])
        contents (see/vertical-panel :items [input-form
                                             img-scroll])
        f (see/frame :content contents
                     :title (.getFile file-ob))]
    (doseq [x (map (partial * pixels-between-lines) (range lines-across))]
      (line-y img x 0 image-height))
    (doseq [y (map (partial * pixels-between-lines) (range lines-down))]
      (line-x img y 0 image-width))
    (see/pack! f)
    (see/show! f)))


(comment

  (dogrid (clojure.java.io/resource "img.jpg"))

)
