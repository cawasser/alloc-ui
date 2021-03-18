(ns alloc-ui.view.ss-grid-page
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [alloc-ui.util.subs]
    [alloc-ui.component.super-simple-grid :as ssg]
    [alloc-ui.component.request-list :as rl]))


(defn- selector
  "selection is the string denoting the collection of requesters associated
  with a single potential-allocation-grid"
  [selection]
  (let [selected-request-set    (rf/subscribe [:selected-request-set])
        selected-request-subset @(rf/subscribe [:selected-request-subset])
        upper-limit             @(rf/subscribe [:selected-request-subset-limit])
        low-limit               (<= selected-request-subset 0)
        up-limit                (<= (dec upper-limit) selected-request-subset)
        not-me                  (not= @selected-request-set selection)
        color                   (if (= selection @selected-request-set)
                                  "is-small is-light is-info"
                                  "is-small is-light")
        inc-fn                  #(do
                                   (prn ":inc-selected-request-subset")
                                   (rf/dispatch [:inc-selected-request-subset]))
        dec-fn                  #(do
                                   (prn ":dec-selected-request-subset")
                                   (rf/dispatch [:dec-selected-request-subset]))
        click-fn                #(do
                                   (prn "click" selection)
                                   (rf/dispatch [:selected-request-set selection]))]
    (prn "selector" selection selected-request-subset upper-limit low-limit up-limit)
    [:div {:style {:display :flex :margin-right "4px"
                   :border  "0.5px" :border-radius ".5px"}}
     [:button.button {:class color :on-click dec-fn :disabled (or not-me low-limit)} "<"]
     [:button.button {:class color :on-click click-fn} (str selection)]
     [:button.button {:class color :on-click inc-fn :disabled (or not-me up-limit)} ">"]]))

(defn ssg-page
  ""
  []
  (let [current-grid         (rf/subscribe [:current-grid])
        potential-grid       (rf/subscribe [:local-grid])
        all-potentials       (rf/subscribe [:all-local-grid])
        requests             (rf/subscribe [:local-requests])
        potential-requests   (rf/subscribe [:local-potential-requests])
        colors               (rf/subscribe [:color-matching])
        combos               (rf/subscribe [:local-combos])
        selected-request-set (rf/subscribe [:selected-request-set])]

    (fn []
      [:section.section {:style {:padding "0.5rem 1.5rem"}}
       [:div.content {:style {:padding "0.70rem 3rem"}}
        [:div.tile.is-ancestor
         [:div.tile.is-8
          [:div#req-grid.container
           ;[:p "grid " (str @current-grid)]
           ;[:p "potential " (str @potential-grid)]
           ;[:p "selected reqs " (str @potential-requests)]
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
          [:div
           [:button.button.is-info.is-outlined "New"]
           [:button.button.is-primary.is-outlined
            {:on-click #(rf/dispatch [:allocate @potential-requests])}
            "Check"]]]]]

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
           [:div {:style {:display :flex :margin-right "4px"}}
            [:div {:style {:width "480px"
                           :height "3em"
                           :display :flex
                           :overflow-x :auto
                           :white-space :nowrap
                           :border "2px outset gray"}}
             ;[:p @selected-request-set]
             (let [possibilities (sort-by count (keys (dissoc @all-potentials #{})))]
               (doall
                 (map (fn [x]
                        ^{:key x} [selector x]) possibilities)))]
            [:button.button.is-danger {:on-click #(prn "COMMIT!")} "Commit!"]]

           [ssg/allocation-grid @potential-grid @colors]]]]]])))



