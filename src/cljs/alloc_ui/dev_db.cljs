(ns alloc-ui.dev-db)


(def current-grid {})

(def potential-grid {})



(def requests {"j" #{[0 0] [[1 2 3] 1]}
               "k" #{[0 2] [3 0]}
               "l" #{[[2 3] 0] [[2 3] 1] [[2 3] 2]}
               "m" #{[[2 3] 2] [[0 1 2] 1]}
               "q" #{[[3 4] 0] [[3 4] 1]}
               "r" #{[0 [2 3]] [0 4]}
               "s" #{[[2 3] 1] [[0 2] 3]}})

(def default-last-service-version "unknown service")

(def default-last-service-sha "00000")

;"m" #{[[2 3] 2] }
;"n" #{[[2 3] #{0 1 2}]}
;"o" #{[[2 3] #{0 1 2}]}})
