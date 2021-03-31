(ns alloc-ui.component.input-element)


(defn input-element
  "An input element that takes in what type of element it is, and the current value that will change with input."
  [id-name-type value]
  [:input.input {:id id-name-type
                 :name id-name-type
                 :class "form-control"
                 :type id-name-type
                 :required ""
                 :value @value
                 :on-change #(reset! value (-> % .-target .-value))}])