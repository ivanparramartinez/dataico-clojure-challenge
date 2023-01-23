(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s]
    [clojure.data.json :as json]
    )
  (:import (java.text SimpleDateFormat)))

(use 'clojure.walk)

(s/def :customer/name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))

;; SOLUTION TO PROBLEM 2
;Function to parse a "dd/MM/yyyy" date string
(def date-format (SimpleDateFormat. "dd/MM/yyyy"))
(defn parse-date [date-str]
  (try
    (.parse date-format date-str)
    (catch Exception e
      (println "Error parsing date:" e))))

(defn json_values_reader [key value]
  (case key
    :issue_date (parse-date value)
    :payment_date (parse-date value)
    :tax_category (if (= value "IVA") :iva value)
    :tax_rate (double value)
    value)
  )

;Make a function to format the json file and replace the keys with the correct ones
(defn replace-items-keys [invoice-map]
  (postwalk-replace
    {:issue_date   :invoice/issue-date
     :customer     :invoice/customer
     :items        :invoice/items
     :company_name :customer/name
     :email        :customer/email
     :price        :invoice-item/price
     :quantity     :invoice-item/quantity
     :sku          :invoice-item/sku
     :taxes        :invoice-item/taxes
     :tax_category :tax/category
     :tax_rate     :tax/rate} invoice-map))

(defn parse-json-invoice [file-name]
  (let [json-invoice (json/read-str (slurp file-name) :key-fn keyword :value-fn json_values_reader)]
    (get (replace-items-keys json-invoice) :invoice)
    ))

;Test the function
(s/valid? ::invoice (parse-json-invoice "invoice.json"))

;; PRINTING RESULTS
(print (s/valid? ::invoice (parse-json-invoice "invoice.json")))