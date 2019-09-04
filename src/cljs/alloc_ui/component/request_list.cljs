(ns alloc-ui.component.request-list
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]))


(defn- submit [requests]
       (rf/dispatch [:allocate (pr-str requests)]))


(defn- any-combo
  ""
  [combos k]
  (for [c combos]
    (contains? c k)))


(defn request-grid
       ""
  [requests combos color-match]

  (let [selected           (r/atom (zipmap (keys requests) (repeat false)))
        potential-requests (r/atom {})]
    (fn []
      [:div.container
       ;[:p "selected" @selected]
       [:table-container
        [:table
         [:thead
          ; TODO figure out how to do a spanning header across the check marks
          [:tr [:th "Include?"] [:th.is-narrow "Work"] [:th.is-narrow "ing C"] [:th.is-narrow "ombo"] [:th "Requestor"] [:th "Requests"]]]
         [:tbody
          (doall
            (for [[k r] (seq requests)]
              (do
                ;(prn "key" k)
                ^{:key k}
                [:tr
                 (if (some true? (any-combo combos k))
                   [:td.is-narrow
                      {:on-click #(do
                                    (swap! selected assoc k (not (get @selected k)))
                                    (if (get @selected k)
                                      (swap! potential-requests assoc k r)
                                      (swap! potential-requests dissoc k)))}
                      (if (get @selected k)
                        [:span.icon.has-text-success.is-small [:i.material-icons :done]]
                        [:span.icon.has-text-success.is-small [:i.material-icons :crop_square]])]
                   [:td.is-narrow [:span.icon.has-text-danger.is-small [:i.material-icons :highlight_off]]])

                 (for [c (any-combo combos k)]
                      (if c
                        [:td.is-narrow (if (get @selected k)
                                         [:i.material-icons.has-text-success :check_circle]
                                         [:i.material-icons.has-text-grey-lighter :done])]
                        [:td.is_narrow ""]))

                 [:td {:style {:background-color (first (get color-match k))
                               :color (second (get color-match k))}}
                  (str k)]

                 (let [txt (for [a r] (str a "     "))]
                      [:td txt])])))]]]
       [:div.content
        [:a.button.is-primary {:on-click #(submit @potential-requests)} "Submit"]]])))
