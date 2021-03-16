(ns alloc-ui.view.ss-grid-page
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [alloc-ui.util.subs]
    [alloc-ui.component.super-simple-grid :as ssg]
    [alloc-ui.component.request-list :as rl]))



(defn ssg-page
  ""
  []
  (let [current-grid       (rf/subscribe [:current-grid])
        potential-grid     (rf/subscribe [:local-grid])
        requests           (rf/subscribe [:local-requests])
        potential-requests (rf/subscribe [:local-potential-requests])
        colors             (rf/subscribe [:color-matching])
        combos             (rf/subscribe [:local-combos])]
    (fn []
      [:section.section {:style {:padding "0.5rem 1.5rem"}}
       [:div.content {:style {:padding "0.70rem 3rem"}}
        [:div.tile.is-ancestor
         [:div.tile.is-8
          [:div#req-grid.container
           ;[:p "grid " (str @current-grid)]
           [:p "selected reqs " (str @potential-requests)]
           ;[:p "colors " @colors]
           [:p.title.is-5 {:style {:padding "0.5rem 4rem"}} "Request Candidates"]
           ;(prn "ssg-page (2)" colors)
           [rl/request-grid
            @requests
            @potential-requests
            @combos
            @colors]]]
         [:div.tile.is-1]
         [:div.tile.is-1
          [:div#req-buttons.container
           [:p.title.is-5.has-text-centered "Buttons"]
           [:button "New"]
           [:button {:on-click #(rf/dispatch [:allocate (pr-str @potential-requests)])}
            "Check"]
           [:button "Commit"]]]]]

       [:div.content {:style {:padding "0.70rem 3rem"}}
        [:div.tile.is-ancestor
         [:div.tile.is-4
          [:div.container
           [:p.title.is-5.has-text-centered "Current Allocation"]
           [ssg/allocation-grid @current-grid @colors]]]
         [:div.tile.is-3]
         [:div.tile.is-4
          [:div.container
           [:p.title.is-5.has-text-centered "Potential Allocation"]
           [ssg/allocation-grid @potential-grid @colors]]]]]])))



