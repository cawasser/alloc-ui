(ns alloc-ui.component.edit-request
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [alloc-ui.component.input-element :as i]
            [cljs.tools.reader.edn :as edn]))


(defn pop-up                                                ;; Name should include modal instead of pop up
  "When a request is 'edited' this modal will pop up"
  [is-active]
  (let [requester (rf/subscribe [:editing])]
    (fn []
      (let [requests  (r/atom @(rf/subscribe [:editing-requests]))]
        [:div.modal (if @is-active {:class "is-active"})

         [:div.modal-background]
         [:div.modal-card
          [:header.modal-card-head
           [:p.modal-card-title "Edit"]
           [:button.delete {:aria-label "close"
                            :on-click   #(reset! is-active false)}]]
          [:section.modal-card-body
           ;[:p "requester " @requester]
           ;[:p "requests " @requests]
           ;[:label "Requester:"
            [:div
             [:label @requester]]
           [:label "Requests:"
            [:div
             [i/input-element "requests" requests]]]]
          [:footer.modal-card-foot
           [:button.button.is-info {:on-click #(do
                                                 (rf/dispatch-sync [:edit-requests @requester (edn/read-string @requests)])
                                                 (rf/dispatch-sync [:editing ""])
                                                 (reset! is-active false))} "Ok"]
           [:button.button {:on-click #(reset! is-active false)} "Cancel"]]]]))))