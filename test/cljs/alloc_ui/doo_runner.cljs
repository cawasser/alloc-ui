(ns alloc-ui.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [alloc-ui.core-test]))

(doo-tests 'alloc-ui.core-test)

