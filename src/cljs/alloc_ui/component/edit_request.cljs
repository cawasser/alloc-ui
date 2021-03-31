(ns alloc-ui.component.edit-request
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [alloc-ui.component.input-element :as i]))


(defn pop-up                                                ;; Name should include modal instead of pop up
  "When a request is 'edited' this modal will pop up"
  [is-active]
  (let [requester @(rf/subscribe [:editing])
        requests  (r/atom nil)]
    (fn []
      [:div.modal (if @is-active {:class "is-active"})
       ;[:p "I was hiding"]
       [:div.modal-background]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Edit"]
         [:button.delete {:aria-label "close"
                          :on-click   #(reset! is-active false)}]]
        [:section.modal-card-body
         [:label "Requester:"
          [:div
           [:label requester]]]
         [:span {:style {:display "inline-block" :width "70px"}}]
         [:label "Requests:"
          [:div
           [i/input-element "requests" requests]]]]
        [:footer.modal-card-foot
         [:button.button.is-info {:on-click #(do
                                               ; update the request for the correct requester
                                               (rf/dispatch-sync [:editing ""])
                                               (reset! is-active false))} "Ok"]
         [:button.button {:on-click #(reset! is-active false)} "Cancel"]]]])))