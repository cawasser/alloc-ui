(ns alloc-ui.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [markdown.core :refer [md->html]]
    [alloc-ui.ajax :as ajax]
    [alloc-ui.util.events]
    [alloc-ui.util.subs]
    [reitit.core :as reitit]
    [alloc-ui.start-up :as start-up]
    [alloc-ui.view.ss-grid-page :as ssg]
    [alloc-ui.view.about-page :as about]
    [alloc-ui.view.heatmap-page :as heatmap]
    [alloc-ui.view.point-grid-page :as point-grid]

    [alloc-ui.util.request-support :as rs])
  (:import goog.History))



(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :active (when (= page @(rf/subscribe [:page])) "active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item.is-size-3 {:href "/" :style {:font-weight :bold}} "Black Hammer "]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click    #(swap! expanded? not)
                  :class       (when @expanded? :is-active)}
                 [:span] [:span] [:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-end
                 [nav-link "#/" "SS Grid" :home]
                 [nav-link "#/heatmap" "Heatmap" :heatmap]
                 [nav-link "#/point-grid" "Point Grid" :point-grid]
                 [nav-link "#/about" "About" :about]]]]))


(defn footer []
  [:footer.is-size-7.has-text-right.has-text-grey-light
   {:style {:padding "0rem 3rem"}}
   "Copyright 2019, Northrop Grumman (v."
   @(rf/subscribe [:last-service-version])
   "."
   @(rf/subscribe [:last-service-sha])
   ")"])

(def pages
  {:home  #'ssg/ssg-page
   :heatmap #'heatmap/heatmap-page
   :point-grid #'point-grid/point-grid-page
   :about #'about/about-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]
   [footer]])


;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))


(defn init! []
  (rf/dispatch-sync [:navigate (reitit/match-by-name start-up/router :home)])
  (ajax/load-interceptors!)

  (rf/dispatch-sync [:init-db])
  (rf/dispatch-sync [:fetch-current-grid])
  (rf/dispatch [:allocate (pr-str @(rf/subscribe [:local-requests]))])

  (start-up/hook-browser-navigation!)
  (mount-components))
