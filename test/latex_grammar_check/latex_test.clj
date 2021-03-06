(ns latex-grammar-check.latex-test
  (:require [clojure.test :refer :all]
            [latex-grammar-check.latex :as latex]))

(deftest tokenize-test
  (is (= [{:node-type :tword, :text "word1", :line 1, :pos 1} 
          {:node-type :twhitespace, :text " ", :line 1, :pos 6} 
          {:node-type :tword, :text "word2", :line 1, :pos 7}]
         (latex/tokenize "word1 word2")))
  (is (= [{:node-type :tword, :text "word1", :line 1, :pos 1} 
          {:node-type :twhitespace, :text "\n", :line 1, :pos 6} 
          {:node-type :tword, :text "word2", :line 2, :pos 1}]
         (latex/tokenize "word1\nword2"))))

(deftest extract-text-test
  (is (= "                   " (latex/extract-text (latex/tokenize "\\author{Luis Martí}"))))
  (is (= "       Luis Martí " (latex/extract-text (latex/tokenize "\\other{Luis Martí}")))))



(run-tests)


;;(println (latex/extract-text (latex/tokenize (slurp "src/sablecc/sample.tex"))))

;;(latex/tokenize "\\author{Luis Martí}")

;;(latex/extract-text (latex/tokenize "\\documentstyle[html]{article} % comment"))

;(latex/tokenize "\\emph{a}")

;(println (latex/extract-text (latex/tokenize "$x$ to the power of $y$ equals 0.")))

;(latex/tokenize "$x$ to the power of $y$ equals 0.")