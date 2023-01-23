(ns Problem1)

(def invoice (clojure.edn/read-string
               (slurp "invoice.edn")))

;this function returns the items that have a tax rate of 19
(defn filter-taxable-items-by-rate [invoice]
  (->> invoice
       :invoice/items
       (filter #(some (fn [%] (= 19 (:tax/rate %))) (get-in % [:taxable/taxes])))))

;this function returns the items that have a retention rate of 1
(defn filter-retentionable-items-by-rate [invoice]
  (->> invoice
       :invoice/items
       (filter #(some (fn [%] (= 1 (:retention/rate %))) (get-in % [:retentionable/retentions])))))


;this function returns the items that have an iva tax rate of 19 or a retention rate of 1, but not both
(defn filter-by-tax-and-retention [invoice]
  (->> invoice
       :invoice/items
       (filter (fn [item]
                 (let [filter-tax (some #(and (= 19 (:tax/rate %)) (= :iva (:tax/category %))) (get-in item [:taxable/taxes])),
                       filter-retention (some #(and (= 1 (:retention/rate %)) (= :ret_fuente (:retention/category %))) (get-in item [:retentionable/retentions]))]
                   (and (or filter-tax filter-retention) (not (and filter-tax filter-retention))))))))

(println "Items with a tax rate of 19: " (filter-taxable-items-by-rate invoice))
(println "Items with a retention rate of 1: " (filter-retentionable-items-by-rate invoice))
(println "Items with a tax rate of 19 or a retention rate of 1, but not both: " (filter-by-tax-and-retention invoice))
