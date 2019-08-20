(ns alloc-ui.dev-db)


(def current-grid [[#{:a} #{} #{} #{} #{}]
                   [#{:a} #{} #{} #{} #{}]
                   [#{} #{:b} #{:b} #{} #{}]
                   [#{} #{:c} #{:c} #{} #{}]
                   [#{} #{} #{} #{} #{}]])

(def potential-grid [[#{:a} #{} #{:e} #{:e} #{}]
                     [#{:a} #{} #{} #{} #{:f}]
                     [#{} #{:b} #{:b} #{} #{:g}]
                     [#{} #{:c} #{:c} #{} #{}]
                     [#{:d} #{:d} #{} #{} #{}]])

(def requests {:j #{[0 0] [[1 2 3] 1]}
               :k #{[0 1] [3 0]}
               :l #{[[2 3] #{0 1 2}]}})
;:m #{[[2 3] #{0 1 2}]}
;:n #{[[2 3] #{0 1 2}]}
;:o #{[[2 3] #{0 1 2}]}})
