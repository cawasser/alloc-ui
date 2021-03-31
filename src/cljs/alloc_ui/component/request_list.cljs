(ns alloc-ui.component.request-list
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [alloc-ui.component.new-requester :as new-requester]
    [alloc-ui.component.edit-request :as edit-request]))

(defn- any-combo
  ""
  [combos k]
  (for [c combos]
    (contains? c k)))


(def is-new (r/atom false))

(defn request-grid
  ""
  [requests potential-requests combos color-match]

  ;(prn "request-grid" requests ;potential-requests combos color-match)
  (let [is-editing (r/atom false)]
    (fn []
      [:div

       [:button.button.is-info.is-outlined {:on-click #(reset! is-new true)} "New"]

       [:div.table-container {:style {:width       "100%"
                                      :height      "15em"
                                      :overflow-y  :auto
                                      :white-space :nowrap
                                      :border      "1px outset gray"}}

        [new-requester/pop-up is-new]
        [edit-request/pop-up is-editing]

        [:table.table
         [:thead
          ; TODO figure out how to do a spanning header across the check marks
          [:tr [:th "Include?"]
           ;(for [c combos]
           ;  ^{:key c} [:th.is-narrow "_"])
           [:th "Requestor"] [:th "Requests"]]]
         [:tbody
          (doall
            (for [[k r] (seq @requests)]
              (do
                ;[:p (str {:key k
                ;          :background-color (first (get color-match k))
                ;          :color            (second (get color-match k))})
                ^{:key k}
                [:tr
                 ;(if (some true? (any-combo combos k))
                 ^{:key (str "inc-" k)}
                 [:td.is-narrow
                  {:on-click #(do
                                (if (contains? @potential-requests k)
                                  (rf/dispatch-sync [:remove-from-local-potential-requests k])
                                  (rf/dispatch-sync [:add-to-local-potential-requests k])))}

                  (if (contains? @potential-requests k)
                    [:span.icon.has-text-success.is-small [:i.material-icons :done]]
                    [:span.icon.has-text-success.is-small [:i.material-icons :crop_square]])]

                 ^{:key (str "all-" k)}
                 [:td {:style {:background-color (first (get @color-match k))
                               :color            (second (get @color-match k))}}
                  (str k)]

                 (let [txt (for [a r] (str a "     "))]
                   ^{:key (str "req-" txt)}
                   [:td {:on-click #(do
                                      (rf/dispatch-sync [:editing k])
                                      (reset! is-editing true))} txt])])))]]]])))

(defn dyn-request-grid
  ""
  [requests potential-requests combos color-match]

  [:div.container
   [:p "dummy"]])

