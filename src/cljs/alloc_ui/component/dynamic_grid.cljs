(ns alloc-ui.component.dynamic-grid
  (:require
    [reagent.core :as reagent]
    ;[cljsjs.fixed-data-table]
    [cljsjs.fixed-data-table-2]
    [cljs-time.format :refer [unparse formatter]]
    ;[cosmo.util.grid-cell-renderers :as custom]
    [com.rpl.specter :as s]))




;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; This dynamic-grid is built upon Facebook's FixedDataGrid React component
;
;(def Table (reagent/adapt-react-class js/FixedDataTable.Table))
;(def Column (reagent/adapt-react-class js/FixedDataTable.Column))
;(def ColumnGroup (reagent/adapt-react-class js/FixedDataTable.ColumnGroup))
;(def Cell (reagent/adapt-react-class js/FixedDataTable.Cell))
;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; PRIVATE
;
;; ------------------
;; Custom Cell Renderers
;
;(defn lat-lon-renderer [places cell]
;  (let [s (str cell)
;        [left right] (clojure.string/split s #"\.")
;        r (str right "000000000000000000")]
;    (str left "." (subs r 0 places))))
;
;
;(defn html-renderer [cell]
;  cell)
;
;
;(defn date-renderer [formatting cell]
;  (unparse (formatter formatting) cell))
;
;(defn check-renderer [cell]
;  "")
;
;
;
;
;
;(defn- convert-renderer-key [k]
;  (cond
;    (= :lat-lon-1 k) #(partial lat-lon-renderer 1)
;    (= :lat-lon-4 k) #(partial lat-lon-renderer 4)
;    (= :html k) #(partial html-renderer)
;    (= :date-MM-dd-YYYY k) #(partial date-renderer "MM/dd/YYYY")))
;
;
;
;(defn- process-columns
;  "lookup the correct (render-function) and attach it as :renderer-fn"
;  [columns]
;  (s/transform [(s/filterer #(keyword? (:renderer %))) s/ALL]
;               (fn [k] (assoc k :renderer-fn (convert-renderer-key (:renderer k))))
;               columns))
;
;
;
;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; PUBLIC
;; TODO with this implementation, columns is not processed in "order" so the columns might be jumbled
;
;(defn grid [{:keys [width height rowHeight headerHeight columns data]}]
;  (prn "grid" @columns data)
;  (let [getter         (fn [k row]
;                         (get row (keyword k)))
;        rowGetter      (fn [x] (get data x))
;        is-resizing    (atom false)
;        make-cell      (fn [args]
;                         (prn "make-cell" args)
;                         (let [{:strs [columnKey rowIndex]} (js->clj args)]
;                           (reagent/as-element [Cell (get-in data [rowIndex columnKey])])))
;        onColumnResize (fn [new-width dataKey]
;                         ;(prn "onColumnResize callback" new-width dataKey)
;                         (reset! columns (update-in @columns [dataKey] assoc :col-width new-width)) ; TODO update app-db
;                         (reset! is-resizing false))]
;
;    (fn []
;      [Table {:width                     width
;              :height                    height
;              :rowHeight                 rowHeight
;              ;:rowGetter                 rowGetter
;              :rowsCount                 (count data)
;              :headerHeight              headerHeight
;              :allowCellsRecycling       true
;              :isColumnResizing          @is-resizing
;              :onColumnResizeEndCallback onColumnResize}
;
;       (doall
;         (map (fn [{:keys [label dataKey fixed col-width resizable renderer]} c]
;                ^{:key dataKey} [Column {:label          label :dataKey dataKey
;                                         :cellDataGetter getter
;                                         :cell           make-cell
;                                         :width          col-width
;                                         :fixed          fixed
;                                         :isResizable    resizable
;                                         :cellRenderer   renderer}])
;              @columns))])))







;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;

(def Table (reagent/adapt-react-class js/FixedDataTable.Table))
(def Column (reagent/adapt-react-class js/FixedDataTable.Column))
(def Cell (reagent/adapt-react-class js/FixedDataTable.Cell))


(defn grid [{:keys [num-rows rowHeight headerHeight columns data]}]
  (let [make-cell    (fn [args]
                       (let [{:strs [columnKey rowIndex]} (js->clj args)
                             cell (get-in data [rowIndex columnKey])]
                         (reagent/as-element [Cell cell #_{:style {:background-color (get cell "bg-color")
                                                                   :color            (get cell "txt-color")}}])))
        total-width  (reduce + (map #(:width %) columns))
        total-height (+ 10 headerHeight (* num-rows rowHeight))]

    [:div.container
     [Table {:width        total-width
             :height       total-height
             :rowHeight    rowHeight
             :rowsCount    (count data)
             :headerHeight headerHeight}
      ; TODO need to assoc in the :cell make-cell pair.
      (doall
        (map (fn [{:keys [header columnKey width resizable renderer]}]
               ^{:key columnKey} [Column {:header    header
                                          :columnKey columnKey
                                          :cell      make-cell
                                          :width     width}])

             columns))]]))



; from https://gist.github.com/frankiesardo/17905c5ec26bfc84df7d
;
;(def Table (reagent/adapt-react-class js/FixedDataTable.Table))
;(def Column (reagent/adapt-react-class js/FixedDataTable.Column))
;(def Cell (reagent/adapt-react-class js/FixedDataTable.Cell))
;
;(defn gen-table
;  "Generate `size` rows vector of 4 columns vectors to mock up the table."
;  [size]
;  (mapv (fn [i]
;          {"number" i
;           "amount" (rand-int 1000)
;           "coeff" (rand)
;           "store" (rand-nth ["Here" "There" "Nowhere" "Somewhere"])})
;        (range 1 (inc size))))
;
;(def table (gen-table 10))
;
;(defn make-cell [args]
;  (let [{:strs [columnKey rowIndex]} (js->clj args)]
;    (reagent/as-element [Cell (get-in table [rowIndex columnKey])])))
;
;(defn grid [_]
;  (prn "grid")
;  [:div.container
;   [Table {:width        600
;           :height       400
;           :rowHeight    50
;           :rowsCount    (count table)
;           :headerHeight 50}
;    [Column {:header "Number" :cell make-cell :columnKey "number" :width 100}]
;    [Column {:header "Amount" :cell make-cell :columnKey "amount" :width 100}]
;    [Column {:header "Coeff" :cell make-cell :columnKey "coeff" :width 100}]
;    [Column {:header "Store" :cell make-cell :columnKey "store" :width 100}]]])
