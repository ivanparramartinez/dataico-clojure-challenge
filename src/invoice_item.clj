(ns invoice-item)
(use 'clojure.test)

(defn- discount-factor [{:invoice-item/keys [discount-rate]
                         :or                {discount-rate 0}}]
  (- 1 (/ discount-rate 100.0)))

(defn subtotal
  [{:invoice-item/keys [precise-quantity precise-price discount-rate]
    :as                item
    :or                {discount-rate 0}}]
  (* precise-price precise-quantity (discount-factor item)))

;function to define Item with quantity, price and discount rate
(defn Item [precise-quantity precise-price discount-rate]
  {:invoice-item/precise-quantity precise-quantity
   :invoice-item/precise-price    precise-price
   :invoice-item/discount-rate    discount-rate})

;function to calculate expected result
(defn expected-result [precise-quantity precise-price discount-rate]
  (double (* precise-quantity precise-price (- 1 (/ discount-rate 100.0)))))

;test cases
(deftest Subtotal-no-discount
  (let [item { :invoice-item/precise-quantity 10
              :invoice-item/precise-price    2.99}
        expected-result (expected-result 10 2.99 0)]
    (is (= expected-result (subtotal item)))))

(deftest Subtotal-with-discount-10-percent
  (
  (let [item { :invoice-item/precise-quantity 10
              :invoice-item/precise-price    2.99
              :invoice-item/discount-rate    10}
        expected-result (expected-result 10 2.99 10)]
    (is (= expected-result (subtotal item))))))

;test with 4 different items with different discount rates and quantities and prices
(deftest Subtotal-with-different-items-and-discount-rates
  (
  (let [item1 { :invoice-item/precise-quantity 10
              :invoice-item/precise-price    2.99
              :invoice-item/discount-rate    10}
        item2 { :invoice-item/precise-quantity 20
              :invoice-item/precise-price    3.99
              :invoice-item/discount-rate    20}
        item3 { :invoice-item/precise-quantity 30
              :invoice-item/precise-price    4.99
              :invoice-item/discount-rate    30}
        item4 { :invoice-item/precise-quantity 40
              :invoice-item/precise-price    5.99
              :invoice-item/discount-rate    40}
        expected-result1 (expected-result 10 2.99 10)
        expected-result2 (expected-result 20 3.99 20)
        expected-result3 (expected-result 30 4.99 30)
        expected-result4 (expected-result 40 5.99 40)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))
    (is (= expected-result4 (subtotal item4))))))

;test with 3 different items with the same price, different quantities and without discount rates, defining each item with the function Item
(deftest Subtotal-with-different-items-and-no-discount-rates
  (
  (let [item1 (Item 10 2.99 0)
        item2 (Item 20 2.99 0)
        item3 (Item 30 2.99 0)
        expected-result1 (expected-result 10 2.99 0)
        expected-result2 (expected-result 20 2.99 0)
        expected-result3 (expected-result 30 2.99 0)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3))))))

;test with 3 equal items with different discount rates, defining each item with the function Item
(deftest Subtotal-with-equal-items-and-different-discount-rates
  (
  (let [item1 (Item 10 2.99 10)
        item2 (Item 10 2.99 20)
        item3 (Item 10 2.99 30)
        expected-result1 (expected-result 10 2.99 10)
        expected-result2 (expected-result 10 2.99 20)
        expected-result3 (expected-result 10 2.99 30)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3))))))

;test with 4 equal items with 10% discount rate, defining each item with the function Item, and comparing their subtotals between each other
(deftest Subtotal-with-equal-items-and-same-discount-rates
  (
  (let [item1 (Item 10 2.99 10)
        item2 (Item 10 2.99 10)
        item3 (Item 10 2.99 10)
        item4 (Item 10 2.99 10)
        expected-result1 (expected-result 10 2.99 10)
        expected-result2 (expected-result 10 2.99 10)
        expected-result3 (expected-result 10 2.99 10)
        expected-result4 (expected-result 10 2.99 10)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))
    (is (= expected-result4 (subtotal item4)))
    (is (= expected-result1 expected-result2))
    (is (= expected-result1 expected-result3))
    (is (= expected-result1 expected-result4)))))


