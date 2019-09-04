(ns alloc-ui.view.ss-grid-page
  (:require
    [re-frame.core :as rf]
    [alloc-ui.component.super-simple-grid :as ssg]
    [alloc-ui.component.request-list :as rl]
    [alloc-ui.util.color-pallet :as cp]
    [alloc-ui.util.request-support :as rs]))



(defn ssg-page
  ""
  []
  (let [current-grid       (rf/subscribe [:current-grid])
        potential-grid     (rf/subscribe [:local-grid])
        requests           (rf/subscribe [:local-requests])
        potential-requests (rf/subscribe [:local-potential-requests])]
    (fn []
      (let [colors (cp/color-match @current-grid @requests)]
        [:section.section {:style {:padding "0.5rem 1.5rem"}}
         [:div.content {:style {:padding "0rem 1rem"}}
          ;[:p "grid " (str @current-grid)]
          ;[:p "reqs " (str @requests)]
          ;[:p "colors " (str colors)]]])))
          [:p.title.is-5 {:style {:padding "0.5rem 4rem"}} "Request Candidates"]
          [rl/request-grid @requests
           potential-requests
           (rs/make-combos @requests @current-grid)
           colors]]
         [:div.content {:style {:padding "0.70rem 3rem"}}
          [:div.tile.is-ancestor
           [:div.tile.is-4
            [:div.container
             [:p.title.is-5.has-text-centered "Current Allocation"]
             [ssg/allocation-grid @current-grid colors]]]
           [:div.tile.is-3]
           [:div.tile.is-4
            [:div.container
             [:p.title.is-5.has-text-centered "Potential Allocation"]
             [ssg/allocation-grid @potential-grid colors]]]]]]))))



