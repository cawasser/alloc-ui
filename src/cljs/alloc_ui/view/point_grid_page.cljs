(ns alloc-ui.view.point-grid-page
  (:require
    [re-frame.core :as rf]
    [alloc-ui.component.point-grid :as pg]
    [alloc-ui.component.request-list :as rl]))


(defn point-grid-page
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
       [:div.content {:style {:padding "0rem 1rem"}}
        ;[:p "grid " (str @current-grid)]
        ;[:p "reqs " (str @requests)]
        ;[:p "colors " (str colors)]]])))
        [:p.title.is-5 {:style {:padding "0.5rem 4rem"}} "Request Candidates"]
        [rl/request-grid
         @requests
         potential-requests
         combos
         colors]]
       [:div.content {:style {:padding "0.70rem 3rem"}}
        [:div.tile.is-ancestor
         [:div.tile.is-5
          [:div.container
           [:p.title.is-5.has-text-centered "Current Allocation"]
           [pg/point-grid @current-grid 500 275 colors]]]
         [:div.tile.is-2]
         [:div.tile.is-5
          [:div.container
           [:p.title.is-5.has-text-centered "Potential Allocation"]
           [pg/point-grid @potential-grid 500 275 colors]]]]]])))
