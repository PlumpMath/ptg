(ns ptg.core
  (:require [mikera.image.core :as img]
            [seesaw.core :as see])
  (:import java.awt.image.BufferedImage))


(let [filename "img.jpg"
      img (img/load-image (clojure.java.io/resource filename))
      image-width (.getWidth img)
      image-height (.getHeight img)
      pixels-between-lines 80
      lines-across (int (/ image-width pixels-between-lines))
      lines-down (int (/ image-height pixels-between-lines))
      line-x (fn [y x0 x1]
               (doseq [x (range x0 x1)]
                 (.setRGB img x y 0)))
      line-y (fn [x y0 y1]
               (doseq [y (range y0 y1)]
                 (.setRGB img x y 0)))]
  (doseq [x (map (partial * pixels-between-lines) (range lines-across))]
    (line-y x 0 image-height))
  (doseq [y (map (partial * pixels-between-lines) (range lines-down))]
    (line-x y 0 image-width))
  (let [img-scroll (see/scrollable (see/label :icon img))
        input-form (see/horizontal-panel
                    :items [(see/label :text "Support size: ")
                            (see/text :columns 1 :text "11")
                            (see/label :text " inches wide by ")
                            (see/text "8.5")
                            (see/label :text " inches high. Square size: ")
                            (see/text "1")
                            (see/label :text " inch.")])
        contents (see/vertical-panel :items [input-form
                                             img-scroll])
        f (see/frame :content contents
                     :minimum-size [640 :by 480]
                     :title filename)]
    (see/pack! f)
    (see/show! f)))
