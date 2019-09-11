(ns alloc-ui.component.request-list
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [alloc-ui.component.dynamic-grid :as grid]))


(defn- any-combo
  ""
  [combos k]
  (for [c combos]
    (contains? c k)))



(defn request-grid
  ""
  [requests potential-requests combos color-match]

  [:div.container
   (prn "request-grid" requests potential-requests combos color-match)
   [:table-container
    [:table
     [:thead
      ; TODO figure out how to do a spanning header across the check marks
      [:tr [:th "Include?"] [:th.is-narrow "Work"] [:th.is-narrow "ing C"] [:th.is-narrow "ombo"] [:th "Requestor"] [:th "Requests"]]]
     [:tbody
      (doall
        (for [[k r] (seq requests)]
          (do
            ;[:p (str {:key k
            ;          :background-color (first (get color-match k))
            ;          :color            (second (get color-match k))})
            ^{:key k}
            [:tr
             (if (some true? (any-combo combos k))
               ^{:key (str "inc-" k)}
               [:td.is-narrow
                {:on-click #(do
                              (if (contains? potential-requests k)
                                (rf/dispatch-sync [:remove-from-local-potential-requests k])
                                (rf/dispatch-sync [:add-to-local-potential-requests k])))}

                (if (contains? potential-requests k)
                  [:span.icon.has-text-success.is-small [:i.material-icons :done]]
                  [:span.icon.has-text-success.is-small [:i.material-icons :crop_square]])]

               ^{:key (str "inc-" k)}
               [:td.is-narrow [:span.icon.has-text-danger.is-small
                               [:i.material-icons :highlight_off]]])

             (doall
               (for [[idx c] (map-indexed vector (any-combo combos k))]
                 (if c
                   ^{:key (str idx "-" k)}
                   [:td.is-narrow (if (contains? potential-requests k)
                                    [:i.material-icons.has-text-success :check_circle]
                                    [:i.material-icons.has-text-grey-lighter :done])]
                   ^{:key (str idx "-" k)}
                   [:td.is_narrow ""])))

             ^{:key (str "all-" k)}
             [:td {:style {:background-color (first (get color-match k))
                           :color            (second (get color-match k))}}
              (str k)]

             (let [txt (for [a r] (str a "     "))]
               ^{:key (str "req-" txt)} [:td txt])])))]]]])

















;(def satellites {"1111" {:iron        "1111" :name "sat-1111"
;                         :type        "Alpha" :in-service "Date"
;                         :bands       ["X" "Y" "Z"]
;                         :inclination 10 :lon 50
;                         :altitude    100}
;                 "2222" {:iron        "2222" :name "sat-2222"
;                         :type        "Alpha" :in-service "Date"
;                         :bands       ["X" "Y" "Z"]
;                         :inclination 10 :lon 50
;                         :altitude    100}})
;
;(def ex-columns [{:header "Number" :columnKey "number" :width 100}
;                 {:header "Amount" :columnKey "amount" :width 100}
;                 {:header "Coeff" :columnKey "coeff" :width 100}
;                 {:header "Store" :columnKey "store" :width 100}])
;
;(defn gen-table
;  "Generate `size` rows vector of 4 columns vectors to mock up the table."
;  [size]
;  (mapv (fn [i]
;          {"number" i
;           "amount" (rand-int 1000)
;           "coeff"  (rand)
;           "store"  (rand-nth ["Here" "There" "Nowhere" "Somewhere"])})
;        (range 1 (inc size))))
;


(def columns
  [{:header "Included?" :columnKey :included :width 100 :fixed true :rendered :check-rendered}
   {:header "a" :columnKey "a" :width 30 :fixed true :rendered :valid-rendered}
   {:header "b" :columnKey "b" :width 30 :fixed true :rendered :valid-rendered}
   {:header "c" :columnKey "c" :width 30 :fixed true :rendered :valid-rendered}
   {:header "Requestor" :columnKey "allocated-to" :width 100}
   {:header "Requests" :columnKey "requests" :width 350}])


(defn- process-requests
  ""
  [requests combos color-match]
  (for [r requests]
    (let [[a rs] r]
      {"included"     :false
       "a"            (any-combo combos a)
       "b"            (any-combo combos a)
       "c"            (any-combo combos a)
       "allocated-to" a
       "requests"     rs
       "bg-color"     (first (get color-match a))
       "txt-color"    (second (get @color-match a))})))


(defn dyn-request-grid
  ""
  [requests potential-requests combos color-match]

  (fn []

    (let [p-reqs (process-requests requests @combos color-match)]
      [:div.container
       [grid/grid {:num-rows     5
                   :rowHeight    30
                   :headerHeight 40
                   :columns      columns
                   :data         (into [] p-reqs)}]])))
