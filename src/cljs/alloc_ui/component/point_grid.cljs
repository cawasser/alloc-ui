(ns alloc-ui.component.point-grid
  (:require
    [thi.ng.geom.svg.core :as svg]
    [thi.ng.geom.svg.adapter :as adapt]
    [thi.ng.geom.viz.core :as viz]
    [thi.ng.color.gradients :as grad]
    [thi.ng.math.noise :as n]))


(defn- ->point-grid []
  {"4-4" {:channel 4, :timeslot 4, :allocated-to #{"z"}}
   "2-4" {:channel 2, :timeslot 4, :allocated-to #{"aa"}}
   "0-0" {:channel 0, :timeslot 0, :allocated-to #{"a"}}
   "1-4" {:channel 1, :timeslot 4, :allocated-to #{"aa"}}
   "3-4" {:channel 3, :timeslot 4, :allocated-to #{"z"}}
   "0-1" {:channel 0, :timeslot 1, :allocated-to #{"a"}}
   "1-3" {:channel 1, :timeslot 3, :allocated-to #{"c"}}
   "1-2" {:channel 1, :timeslot 2, :allocated-to #{"b"}}
   "2-2" {:channel 2, :timeslot 2, :allocated-to #{"b"}}
   "2-3" {:channel 2, :timeslot 3, :allocated-to #{"c"}}
   "3-3" {:channel 3, :timeslot 3, :allocated-to #{"z"}}})



(defn- min-max
  ""
  [k data]

  (prn "min-max" k data)
  (prn (map #(get % k) (vals data)))
  (prn (-> (map #(get % k) (vals data))
           ((fn [x]
              [(apply min x) (apply max x)]))))

  (-> (map #(get % k) (vals data))
      ((fn [x]
         [(inc (apply min x)) (inc (apply max x))]))))



(defn- spec [data colors]
  (let [p-grid (map (fn [x]
                      [(inc (:channel x)) (inc (:timeslot x)) (-> x :allocated-to first)])
                    (vals data))]
    (prn "spec" p-grid)
    {:grid {:attribs {:stroke "lightgray"}
            :minor-x true
            :minor-y true}
     :data [{:values  p-grid
             :attribs {:fill "#f60" :stroke "#fff"}
             :shape   (viz/svg-triangle-down 10)
             :layout  viz/svg-scatter-plot}]}))


(def scale 10)
(def range-scale 50)


(defn- draw-point-grid [data width height colors opts]
  (prn "draw-point-grid" data width height)
  (let [[min-ch max-ch] (min-max :channel data)
        [min-ts max-ts] (min-max :timeslot data)]

    (->> {:x-axis (viz/linear-axis
                    {:domain [min-ch max-ch]
                     :range  [(* min-ch range-scale) (* max-ch range-scale)]
                     :scale  scale
                     :pos    0})
          :y-axis (viz/linear-axis
                    {:domain      [min-ts max-ts]
                     :range       [(* min-ts range-scale) (* max-ts range-scale)]
                     :scale       scale
                     :major       100
                     :minor       50
                     :pos         0
                     :label-dist  15
                     :label-style {:text-anchor "end"}})}

         (merge (spec data colors) (first opts))

         (viz/svg-plot2d-cartesian)
         (svg/svg {:width width :height height})
         (adapt/all-as-svg))))

;---------------------------------
;---------------------------------

(defn point-grid [data width height colors & [opts]]
  (if (empty? data)
    (draw-point-grid (->point-grid) width height colors opts)
    (draw-point-grid data width height colors opts)))