(ns alloc-ui.dev-db)


(def current-grid {})

(def potential-grid {})



; How do "requests" work? What is the format for a valid request?

; request is a set of pairs, channel followed by time-slot.
;   for example: "a" would like channel 0 at time-slot 0:
;          #{[0 0]}
;
;   "b" would like channel 1 at time-slot 1 and channel 2 at timeslot 2:
;          #{[1 1] [2 2]}
;
; some flexibility is available (and some is planned but not yet implemented)
;
; (WORKING)
; A requester might be able to one one of a small set of channel at a time-slot,
; for example "a" can use either channel 2 OR 3 at time-slot 0:
;          #{[[2 3] 0]}
;
; "b" can use either 3 OR 4 at slot 1 AND slots 1, 2 OR 3 at slot 3:
;          #{[3 4] 1] [[1 2 3] 3]}
;
; (NOT working)
; A requester might be flexible as to time, but NOT channel: "b" needs channel 2 at
; either slot 1 OR 2:
;          #{[2 [1 2]]}


(def requests {"j" #{[0 0] [[1 2 3] 1]}
               "k" #{[0 2] [3 0]}
               "l" #{[[2 3] 0] [[2 3] 1] [[2 3] 2]}
               "m" #{[[2 3] 2] [[0 1 2] 1]}
               "q" #{[[3 4] 0] [[3 4] 1]}
               "r" #{[0 [2 3]] [0 4]}
               "s" #{[[2 3] 1] [[0 2] 3]}
               "x" #{[0 5] [0 6]}})

(def default-last-service-version "unknown service")

(def default-last-service-sha "00000")

;"m" #{[[2 3] 2] }
;"n" #{[[2 3] #{0 1 2}]}
;"o" #{[[2 3] #{0 1 2}]}})
