(ns alloc-ui.util.color-pallet)

(def color-pallet [["green" "white"] ["blue" "white"] ["orange" "black"]
                   ["grey" "white"] ["cornflowerblue" "white"] ["darkcyan" "white"]
                   ["goldenrod" "black"] ["khaki" "black"] ["deepskyblue" "black"]
                   ["navy" "white"] ["red" "white"] ["orangered" "white"]])


(defn- grid-keys [grid]
  (if (not (empty? grid))
    (remove nil?
            (into #{}
                  (map (fn [[_ res]]
                         (first (:allocated-to res)))
                       grid)))))


(defn color-match [grid requestors]
  (zipmap (concat (grid-keys grid) (keys requestors)) color-pallet))

