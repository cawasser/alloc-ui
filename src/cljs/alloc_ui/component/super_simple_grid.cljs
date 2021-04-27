(ns alloc-ui.component.super-simple-grid)


(defn gen-id
  ([ch ts]
   (str ch "-" ts))
  ([[ch ts]]
   (str ch "-" ts)))


(defn- calc-grid-extents
  "takes a grid and returns a vector of [width height]"
  [grid]

  ;(prn "-------------- calc-grid-extents" grid)
  (if (or (nil? grid) (empty? grid))
    [3 3]

    (let [width (->> grid
                  vals
                  (map :channel)
                  (apply max)
                  inc)
          height (->> grid
                   vals
                   (map :timeslot)
                   (apply max)
                   inc)]

      [width height])))

(defn allocation-grid [grid color-match]
  ; TODO: make the grid whatever size it needs to be
  (let [[w h] (calc-grid-extents grid)]
    [:table.table
     [:thead
      [:tr [:th ""]
       (for [x (range w)]
         ^{:key x} [:th.has-text-centered x])]]
     [:tbody
      (for [y (range h)]
        ^{:key y}
        [:tr [:th (str y)]
         (for [x (range w)]
           (let [t (first (get-in grid [(gen-id x y) :allocated-to]))]
             (if (nil? t)
               ^{:key (str x "-" 0)} [:td ""]
               ^{:key (str x "-" t)} [:td.has-text-centered
                                      {:style {:background-color (first (get color-match t))
                                               :color            (second (get color-match t))}}
                                      (str t)])))])]]))
