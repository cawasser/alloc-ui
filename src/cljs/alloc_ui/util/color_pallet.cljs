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
  (do
    ;(prn "color-match (1)" grid (grid-keys grid) requestors)
    (if (not (empty? (grid-keys grid)))
      (let [c-reqs (grid-keys grid)]
        (zipmap (concat c-reqs (keys requestors)) color-pallet)))))

