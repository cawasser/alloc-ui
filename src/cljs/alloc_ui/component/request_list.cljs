(ns alloc-ui.component.request-list
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    ["react-data-grid" :refer (TextEditor) :default DataGrid]))


;
; NOTE: react-data-grid has undergone a significant BREAKING change recently,
; and the "easy to find" documentation sit is for the OLD version! Be sure to use
; this link:
;       https://github.com/adazzle/react-data-grid
; and you'll need to poke through some JS code to figure out how to use it



(def columns [{:key "include", :name "Include?"}
              {:key "requester", :name "Requester" :editor TextEditor}
              {:key "requests", :name "Requests" :editor TextEditor}])


(defn- process-requests [c r]
  (let [ids (map (fn [{k :key}] (keyword k)) c)
        vals (map (fn [[k v]] [true k (pr-str v)]) r)]
    (map #(zipmap ids %) vals)))



(defn- rowKeyGetter [r]
  (:requester r))

(defn- onRowsUpdated [r]
  (let [c (js->clj r)]
   (prn "onRowsUpdated" (count r) "////" (count c))))


(defn- onSelectedRowsUpdated [r]
  (let [c (js->clj r)]
    (doall
      (map #(prn "onSelectedRowsUpdated" %) r))))

(defn request-grid
  ""
  [requests potential-requests combos color-match]
  (let [r (process-requests columns requests)]
    [:div
     [:p "orig requests " requests]
     [:p "new requests " r]
     [:> DataGrid
      {:columns   columns
       :rows r
       :rowGetter #(get r %)
       ;:rowKeyGetter rowKeyGetter
       :onRowsChange onRowsUpdated
       :onSelectedRowsChange onSelectedRowsUpdated
       :rowsCount (count r)
       :minHeight 150}]]))


(defn- any-combo
  ""
  [combos k]
  (for [c combos]
    (contains? c k)))


;(defn request-grid
;  ""
;  [requests potential-requests combos color-match]
;
;  ;(prn "request-grid" requests ;potential-requests combos color-match)
;  [:div.table-container {:style {:width       "100%"
;                                 :height      "15em"
;                                 :overflow-y  :auto
;                                 :white-space :nowrap
;                                 :border      "1px outset gray"}}
;   [:table.table
;    [:thead
;     ; TODO figure out how to do a spanning header across the check marks
;     [:tr [:th "Include?"]
;      ;(for [c combos]
;      ;  ^{:key c} [:th.is-narrow "_"])
;      [:th "Requestor"] [:th "Requests"]]]
;    [:tbody
;     (doall
;       (for [[k r] (seq requests)]
;         (do
;           ;[:p (str {:key k
;           ;          :background-color (first (get color-match k))
;           ;          :color            (second (get color-match k))})
;           ^{:key k}
;           [:tr
;            ;(if (some true? (any-combo combos k))
;            ^{:key (str "inc-" k)}
;            [:td.is-narrow
;             {:on-click #(do
;                           (if (contains? potential-requests k)
;                             (rf/dispatch-sync [:remove-from-local-potential-requests k])
;                             (rf/dispatch-sync [:add-to-local-potential-requests k])))}
;
;             (if (contains? potential-requests k)
;               [:span.icon.has-text-success.is-small [:i.material-icons :done]]
;               [:span.icon.has-text-success.is-small [:i.material-icons :crop_square]])]
;
;            ^{:key (str "all-" k)}
;            [:td {:style {:background-color (first (get color-match k))
;                          :color            (second (get color-match k))}}
;             (str k)]
;
;            (let [txt (for [a r] (str a "     "))]
;              ^{:key (str "req-" txt)} [:td txt])])))]]])

















(defn dyn-request-grid
  ""
  [requests potential-requests combos color-match]

  (fn []
    [:p "dummy"]))
