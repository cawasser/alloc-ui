(ns alloc-ui.component.heatmap
  (:require [thi.ng.geom.svg.core :as svg]
            [thi.ng.geom.svg.adapter :as adapt]
            [thi.ng.geom.viz.core :as viz]
            [thi.ng.color.gradients :as grad]
            [thi.ng.math.noise :as n]))


;(defn circle-cell
;      [a b c d col] (svg/circle (gu/centroid [a b c d])
;                                (* 0.5 (g/dist a b)) {:fill col})) ; 0.5


(defn- ->heatmap []
      (->> (for [y (range 10) x (range 50)]
                    (n/noise2 (* x 0.1) (* y 0.25)))
           (viz/matrix-2d 50 10)))

;---------------------------------
;---------------------------------

(defn- heatmap-spec [data id]
      {:matrix        data
       :value-domain  (viz/value-domain-bounds data)
       :palette       (->> id
                           (grad/cosine-schemes)
                           (apply grad/cosine-gradient 100))
       :palette-scale viz/linear-scale
       :layout        viz/svg-heatmap})


(defn- draw-heatmap [data width height id opts]
      (let []
           (->> {:x-axis (viz/linear-axis
                           {:domain [0 50]
                            :range [50 1110]
                            :major 10
                            :minor 5
                            :pos 336
                            :scale 2.0})
                 :y-axis (viz/linear-axis
                           {:domain [0 10]
                            :range [336 10]
                            :major 1
                            :pos 50
                            :label-dist 15
                            :label {:text-anchor "end"}
                            :scale 2.0})
                 :data [(merge (heatmap-spec data id) (first opts))]}

                (viz/svg-plot2d-cartesian)
                (svg/svg {:width width :height height})
                (adapt/all-as-svg))))

;---------------------------------
;---------------------------------

(defn heatmap [data width height id & [opts]]
      (if (empty? data)
        (draw-heatmap (->heatmap) width height id opts)
        (draw-heatmap data width height id opts)))

