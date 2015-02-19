(ns ptg.core
  (:gen-class)
  (:require [mikera.image.core :as img]
            [seesaw.core :as see])
  (:import java.awt.image.BufferedImage))


(defn gridlines
  "
  Generate grid lines pattern for a given support (target area) given
  an input image size (width/height in pixels) and output/support size
  (inches, cm, GeV**-1, etc. -- some distance units) as well as a grid
  square width in the same units; output a map of xline and yline
  vectors where the first argument is the fixed x or y coordinate of
  the line, and the second and third argument are the X or Y values
  for the endpoints of the lines.  The vectors of lines include (in
  first position) a closing or final line, regardless of whether that
  line is a whole grid step away from the next-to-last.

  Example:
  (gridlines 2 2 1 100 100)

  ;;=>
  {:ylines ([99 0 100] [0 0 100] [50 0 100]),
   :xlines ([99 0 100] [0 0 100] [50 0 100])}

  "
  [support-width support-height support-grid-spacing image-width image-height]
  (let [ppi (min (/ image-width support-width)
                 (/ image-height support-height))

        pixels-per-square (* ppi support-grid-spacing)

        lines-across (/ (* support-width ppi) pixels-per-square)
        lines-down (/ (* support-height ppi) pixels-per-square)

        ;; Only generate grid covering canvas/support area...
        pixels-horiz-on-support (* ppi support-width)
        pixels-vert-on-support (* ppi support-height)
        margin-side (/ (- image-width pixels-horiz-on-support) 2)
        margin-top (/ (- image-height pixels-vert-on-support) 2)]
    {:ylines
     (conj
      (for [x (map #(+ margin-side (* pixels-per-square %))
                   (range lines-across))]
        [x margin-top (+ margin-top pixels-vert-on-support)])
      [(dec (+ margin-side pixels-horiz-on-support))
       margin-top (+ margin-top pixels-vert-on-support)])

     :xlines
     (conj
      (for [y (map #(+ margin-top (* pixels-per-square %))
                   (range lines-down))]
        [y margin-side (+ margin-side pixels-horiz-on-support)])
      [(dec (+ margin-top pixels-vert-on-support))
       margin-side (+ margin-side pixels-horiz-on-support)])}))


(def f (see/frame))
(def support-width 11)
(def support-height 5)
(def support-grid-spacing 1)
(see/config! f :title "hi")
(see/config! f :content (see/text :text "da bazzr"))
(def width-txt (see/text (str support-width)))
(def height-txt (see/text (str support-height)))
(def grid-txt (see/text (str support-grid-spacing)))


(def lbl (see/label))


(def input-form
  (see/horizontal-panel
   :items
   [(see/label :text " Support size: ") width-txt
    (see/label :text " inches/cm wide by ") height-txt
    (see/label :text " inches/cm high. Square size: ") grid-txt
    (see/label :text " inch/cm. ")]))


(def img-scroll (see/scrollable lbl))


(defn draw-image-with-lines [support-width
                             support-height
                             support-grid-spacing]
  (let [img (->> "img.jpg"
                 clojure.java.io/resource
                 img/load-image)
        {:keys [xlines ylines]}
        (gridlines support-width support-height support-grid-spacing
                   (.getWidth img) (.getHeight img))
        contents (see/vertical-panel :items [input-form img-scroll])]
    (doseq [[y x0 x1] xlines] (line-x img y x0 x1))
    (doseq [[x y0 y1] ylines] (line-y img x y0 y1))
    (see/config! f :content contents)
    (see/config! lbl :icon img)
    (see/pack! f)
    (see/show! f)))


(defn callback [e]
  (if (= 10 (.getKeyCode e))
    (let [width (Integer. (see/config width-txt :text))
          height (Integer. (see/config height-txt :text))
          grid-size (Integer. (see/config grid-txt :text))]
      (draw-image-with-lines width height grid-size))))


(see/listen width-txt :key-pressed callback)
(see/listen height-txt :key-pressed callback)
(see/listen grid-txt :key-pressed callback)


(see/native!)
(draw-image-with-lines 12 10 3)

