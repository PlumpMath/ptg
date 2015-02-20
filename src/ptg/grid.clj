(ns ptg.grid)


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
