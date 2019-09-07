(ns alloc-ui.util.request-support
  (:require
    [re-frame.core :as rf]))

(defn make-combos
  "combos will be use to determining all the possible combinations of the candidate requests
   that produce workable solutions"
  [requests grid]

  ; TODO replace static answer with real calls to the service

  (rf/dispatch [:new-combo #{"k"}])
  (rf/dispatch [:new-combo #{"l"}])
  (rf/dispatch [:new-combo #{"k" "l"}]))

