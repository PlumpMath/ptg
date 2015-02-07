(ns mikera.image.demo
  (:require [mikera.image.core :as img]
            [seesaw.core :as see])
  (:import java.awt.image.BufferedImage))


(set! *unchecked-math* true)


(let [img (img/load-image (clojure.java.io/resource "img.jpg"))
      image-width (.getWidth img)
      image-height (.getHeight img)
      lines-across 10
      lines-down 10
      pixels-between-lines-across (/ image-width lines-across)
      pixels-between-lines-down (/ image-height lines-down)
      line-x (fn [y x0 x1]
               (doseq [x (range x0 x1)]
                 (.setRGB img x y 0)))
      line-y (fn [x y0 y1]
               (doseq [y (range y0 y1)]
                 (.setRGB img x y 0)))]
  (doseq [x (map (partial * pixels-between-lines-across) (range lines-across))]
    (line-y x 0 image-height))
  (doseq [y (map (partial * pixels-between-lines-down) (range lines-down))]
    (line-x y 0 image-width))
  ;(img/show img :zoom 0.5)
  (let [f (see/frame :content (see/label :icon img))]
    (see/show! f)))
