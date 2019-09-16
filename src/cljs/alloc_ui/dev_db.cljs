(ns alloc-ui.dev-db)


(def current-grid {})

(def potential-grid {#{} {"4-4" {:channel 4, :timeslot 4, :allocated-to #{"z"}}
                          "2-4" {:channel 2, :timeslot 4, :allocated-to #{"aa"}}
                          "0-0" {:channel 0, :timeslot 0, :allocated-to #{"a"}}
                          "1-4" {:channel 1, :timeslot 4, :allocated-to #{"aa"}}
                          "3-4" {:channel 3, :timeslot 4, :allocated-to #{"z"}}
                          "0-1" {:channel 0, :timeslot 1, :allocated-to #{"a"}}
                          "1-3" {:channel 1, :timeslot 3, :allocated-to #{"c"}}
                          "1-2" {:channel 1, :timeslot 2, :allocated-to #{"b"}}
                          "2-2" {:channel 2, :timeslot 2, :allocated-to #{"b"}}
                          "2-3" {:channel 2, :timeslot 3, :allocated-to #{"c"}}
                          "3-3" {:channel 3, :timeslot 3, :allocated-to #{"z"}}}
                     #{"k"} {"4-4" {:channel 4, :timeslot 4, :allocated-to #{"z"}},
                             "2-4" {:channel 2, :timeslot 4, :allocated-to #{"aa"}},
                             "0-0" {:channel 0, :timeslot 0, :allocated-to #{"a"}},
                             "0-2" {:channel 0, :timeslot 2, :allocated-to #{"k"}},
                             "3-0" {:channel 3, :timeslot 0, :allocated-to #{"k"}}
                             "1-4" {:channel 1, :timeslot 4, :allocated-to #{"aa"}},
                             "3-4" {:channel 3, :timeslot 4, :allocated-to #{"z"}},
                             "0-1" {:channel 0, :timeslot 1, :allocated-to #{"a"}},
                             "1-3" {:channel 1, :timeslot 3, :allocated-to #{"c"}},
                             "1-2" {:channel 1, :timeslot 2, :allocated-to #{"b"}},
                             "2-2" {:channel 2, :timeslot 2, :allocated-to #{"b"}},
                             "2-3" {:channel 2, :timeslot 3, :allocated-to #{"c"}},
                             "3-3" {:channel 3, :timeslot 3, :allocated-to #{"z"}}}
                     #{"l"} {"4-4" {:channel 4, :timeslot 4, :allocated-to #{"z"}}
                             "2-4" {:channel 2, :timeslot 4, :allocated-to #{"aa"}}
                             "0-0" {:channel 0, :timeslot 0, :allocated-to #{"a"}}
                             "1-4" {:channel 1, :timeslot 4, :allocated-to #{"aa"}}
                             "3-0" {:channel 3, :timeslot 0, :allocated-to #{"l"}}
                             "3-1" {:channel 3, :timeslot 1, :allocated-to #{"l"}}
                             "3-2" {:channel 3, :timeslot 2, :allocated-to #{"l"}}
                             "3-4" {:channel 3, :timeslot 4, :allocated-to #{"z"}}
                             "0-1" {:channel 0, :timeslot 1, :allocated-to #{"a"}}
                             "1-3" {:channel 1, :timeslot 3, :allocated-to #{"c"}}
                             "1-2" {:channel 1, :timeslot 2, :allocated-to #{"b"}}
                             "2-2" {:channel 2, :timeslot 2, :allocated-to #{"b"}}
                             "2-3" {:channel 2, :timeslot 3, :allocated-to #{"c"}}
                             "3-3" {:channel 3, :timeslot 3, :allocated-to #{"z"}}}
                     #{"l" "k"} {"4-4" {:channel 4, :timeslot 4, :allocated-to #{"z"}}
                                 "2-4" {:channel 2, :timeslot 4, :allocated-to #{"aa"}}
                                 "2-0" {:channel 2, :timeslot 0, :allocated-to #{"l"}}
                                 "0-2" {:channel 0, :timeslot 2, :allocated-to #{"k"}}
                                 "0-0" {:channel 0, :timeslot 0, :allocated-to #{"a"}}
                                 "1-4" {:channel 1, :timeslot 4, :allocated-to #{"aa"}}
                                 "3-0" {:channel 3, :timeslot 0, :allocated-to #{"k"}}
                                 "3-1" {:channel 3, :timeslot 1, :allocated-to #{"l"}}
                                 "3-2" {:channel 3, :timeslot 2, :allocated-to #{"l"}}
                                 "3-4" {:channel 3, :timeslot 4, :allocated-to #{"z"}}
                                 "0-1" {:channel 0, :timeslot 1, :allocated-to #{"a"}}
                                 "1-3" {:channel 1, :timeslot 3, :allocated-to #{"c"}}
                                 "1-2" {:channel 1, :timeslot 2, :allocated-to #{"b"}}
                                 "2-2" {:channel 2, :timeslot 2, :allocated-to #{"b"}}
                                 "2-3" {:channel 2, :timeslot 3, :allocated-to #{"c"}}
                                 "3-3" {:channel 3, :timeslot 3, :allocated-to #{"z"}}}})



(def requests {"j" #{[0 0] [[1 2 3] 1]}
               "k" #{[0 2] [3 0]}
               "l" #{[[2 3] 0] [[2 3] 1] [[2 3] 2]}})

(def default-last-service-version "unknown service")

(def default-last-service-sha "00000")

;"m" #{[[2 3] #{0 1 2}]}
;"n" #{[[2 3] #{0 1 2}]}
;"o" #{[[2 3] #{0 1 2}]}})
