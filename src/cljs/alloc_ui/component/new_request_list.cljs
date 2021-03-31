(ns alloc-ui.component.new-request-list
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



(defn dyn-request-grid
  ""
  [requests potential-requests combos color-match]

  [:p "dummy"])
