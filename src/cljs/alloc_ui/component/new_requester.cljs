(ns alloc-ui.component.new-requester
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [alloc-ui.component.input-element :as i]))


(defn pop-up                                                ;; Name should include modal instead of pop up
  "When a request is 'New'd' this modal will pop up"
  [is-active]
  (let [requester (r/atom nil)
        requests  (r/atom nil)]
    (fn []
      [:div.modal (if @is-active {:class "is-active"})
       ;[:p "I was hiding"]
       [:div.modal-background]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "New Requester"]
         [:button.delete {:aria-label "close"
                          :on-click   #(reset! is-active false)}]]
        [:section.modal-card-body
         [:label "Requester:"
          [:div
           [i/input-element "requests" requester]]]
         [:span {:style {:display "inline-block" :width "70px"}}]
         [:label "Requests:"
          [:div
           [i/input-element "requests" requests]]]]
        [:footer.modal-card-foot
         [:button.button.is-info {:on-click #(do
                                               ; add a new requester entry
                                               (reset! is-active false))} "Ok"]
         [:button.button {:on-click #(reset! is-active false)} "Cancel"]]]])))