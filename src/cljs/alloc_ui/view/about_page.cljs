(ns alloc-ui.view.about-page)

(def cards {:clojure {:name "Clojure"
                      :lg-image ""
                      :sm-image "https://clojure.org/images/clojure-logo-120b.png"
                      :dev-name "Rich Hickey"
                      :dev-twitter "@richhickey"
                      :content "Clojure is a robust, practical, and fast programming language with a set of useful features that together form a simple, coherent, and powerful tool."
                      :link "https://clojure.org/index"}
            :leiningen {:name "Leiningen"
                        :lg-image ""
                        :sm-image "https://leiningen.org/img/leiningen.jpg"
                        :dev-name ""
                        :dev-twitter ""
                        :content "Leiningen is a build automation and dependency management tool for the simple configuration of software projects written in the Clojure programming language."
                        :link "https://leiningen.org"}
            :reagent {:name "Reagent"
                      :lg-image ""
                      :sm-image "https://github.com/reagent-project/reagent/raw/master/logo.png"
                      :dev-name ""
                      :dev-twitter ""
                      :content "Reagent provides a minimalistic interface between ClojureScript and React. It allows you to define efficient React components using nothing but plain ClojureScript functions and data, that describe your UI using a Hiccup-like syntax."
                      :link "https://reagent-project.github.io"}
            :re-frame {:name "Re-frame"
                       :lg-image ""
                       :sm-image "https://github.com/Day8/re-frame/raw/master/images/logo/re-frame_128w.png?raw=true"
                       :dev-name ""
                       :dev-twitter ""
                       :content "re-frame is a pattern for writing SPAs in ClojureScript, using Reagent."
                       :link "https://github.com/Day8/re-frame"}
            :bulma {:name "Bulma"
                    :lg-image ""
                    :sm-image "https://bulma.io/images/bulma-logo.png"
                    :dev-name ""
                    :dev-twitter ""
                    :content "Bulma is a free, open source CSS framework based on Flexbox"
                    :link "https://bulma.io/"}})

(def empty-card {:empty-card {:name ""
                              :lg-image ""
                              :sm-image ""
                              :dev-name ""
                              :dev-twitter ""
                              :content ""
                              :link ""}})




(defn- make-card
       ""
  [context]

  [:div.card
   [:header.card-header
    [:div.media-left
     [:figure.image.is-48x48
      [:img {:src (:sm-image context) :alt "Placeholder image"}]]]
    [:p.card-header-title.is-3 (:name context)]]
   [:div.card-content
    [:div.content  (:content context)]
    [:a (:link context)]]])


(defn about-page []
      [:section.section>div.container>div.content
       [:div.tile.is-ancestor.is-vertical
        (for [c (partition-all 3 (vals cards))]
             (do
               [:div.tile.is-parent
                [:div.tile.is-4 (make-card (nth c 0 empty-card))]
                [:div.tile.is-4 (make-card (nth c 1 empty-card))]
                [:div.tile.is-4 (make-card (nth c 2 empty-card))]]))]])
