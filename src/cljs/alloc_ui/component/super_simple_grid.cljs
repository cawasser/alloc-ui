(ns alloc-ui.component.super-simple-grid)


(defn gen-id
  ([ch ts]
   (str ch "-" ts))
  ([[ch ts]]
   (str ch "-" ts)))


(defn allocation-grid [grid color-match]
  ; TODO: make the grid whatever size it needs to be
  (let [w 5 h 5]
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
