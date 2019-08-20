(ns alloc-ui.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [alloc-ui.ajax :as ajax]
    [alloc-ui.events]
    [reitit.core :as reitit]
    [clojure.string :as string]
    [alloc-ui.start-up :as start-up])
  (:import goog.History))


(def color-match {"a" ["green" "white"] "b" ["blue" "white"] "c" ["orange" "white"]
                  "d" ["grey" "white"] "e" ["cornflowerblue" "white"] "f" ["darkcyan" "white"]
                  "g" ["goldenrod" "black"] "j" [] "k" [] "l" [] "z" ["red" "white"]})




(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :active (when (= page @(rf/subscribe [:page])) "active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "BLACK HAMMER - alloc-ui"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click    #(swap! expanded? not)
                  :class       (when @expanded? :is-active)}
                 [:span] [:span] [:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-end
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn footer []
  [:footer.is-size-7.has-text-right.has-text-grey-light
   {:style {:padding "0rem 3rem"}}
   "Black Hammer. Copyright 2019, Northrop Grumman"])



(defn request-grid [requests]
  (let [selected (r/atom (zipmap (keys requests) (repeat false)))]
    (fn []
      [:div.container
       ;[:p "selected = " @selected]
       [:table-container
        [:table
         [:thead
          [:tr [:th "Include?"] [:th "Requestor"] [:th "Requests"]]]
         [:tbody
          (doall
            (for [[k r] (seq requests)]
              ^{:key k}
              [:tr
               [:td {:on-click #(do
                                  (prn "clicked!")
                                  (swap! selected assoc k (not (get @selected k))))}
                (if (get @selected k)
                  [:span.icon.has-text-success.is-small [:i.material-icons :done]]
                  [:span.icon.has-text-success.is-small [:i.material-icons :crop_square]])]
               [:td (str k)]
               (let [txt (for [a r] (str a "     "))]
                 [:td txt])]))]]]])))



#_[:input {:type "text"} txt]




(defn allocation-grid [grid color-match]
  (let [w (count (first grid))
        h (count grid)]
    [:table.is-hoverable
     [:thead
      [:tr [:th ""]
       (for [x (range w)] ^{:key x} [:th.has-text-centered x])]]
     [:tbody
      (for [y (range h)]
        ^{:key y}
        [:tr [:th (str y)]
         (for [x (range w)]
           (let [t (first (get-in grid [y x]))]
             (if (nil? t)
               ^{:key (str x "-" 0)} [:td ""]
               ^{:key (str x "-" t)} [:td.has-text-centered
                                      {:style {:background-color (first (get color-match t))
                                               :color            (second (get color-match t))}}
                                      (str t)])))])]]))



(defn home-page []
  (let [current-grid   (rf/subscribe [:current-grid])
        potential-grid (rf/subscribe [:local-grid])
        requests       (rf/subscribe [:local-requests])]
    (fn []
      [:section.section {:style {:padding "0.5rem 1.5rem"}}
       [:div.container
        [:section.hero.is-bold.is-primary
         [:div.hero-body {:style {:padding "1rem 1.5rem"}}
          [:h1.title "Black Hammer"]
          [:p.subtitle.is-size-7 "Copyright 2019, Northrop Grumman"]]]]
       [:div.content {:style {:padding "1rem 3rem"}}
        [:p.title.is-4 "Your Requests"]
        [request-grid @requests]
        [:div.content
         [:a.button.is-primary {:on-click #()} "Submit"]]]
       [:div.content {:style {:padding "0.70rem 3rem"}}
        [:div.tile.is-ancestor
         [:div.tile.is-4
          [:div.container
           [:p.title.is-5.has-text-centered "Current Allocation"]
           [allocation-grid @current-grid color-match]]]
         [:div.tile.is-3]
         [:div.tile.is-4
          [:div.container
           [:p.title.is-5.has-text-centered "Potential Allocation"]
           [allocation-grid @potential-grid color-match]]]]]])))



(def pages
  {:home  #'home-page
   :about #'about-page})

(defn page []
  [:div
   [(pages @(rf/subscribe [:page]))]])


;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))


(defn init! []
  (rf/dispatch-sync [:navigate (reitit/match-by-name start-up/router :home)])

  (ajax/load-interceptors!)
  (rf/dispatch-sync [:init-db])
  (rf/dispatch [:fetch-current-grid])
  (start-up/hook-browser-navigation!)
  (mount-components))
