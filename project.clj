(defproject latex-grammar-check "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [io.pedestal/pedestal.service "0.1.3"]

                 ;; Remove this line and uncomment the next line to
                 ;; use Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.1.3"]
                 ;; [io.pedestal/pedestal.tomcat "0.1.3"]

                 ;; Logging
                 [ch.qos.logback/logback-classic "1.0.7"]
                 [org.slf4j/jul-to-slf4j "1.7.2"]
                 [org.slf4j/jcl-over-slf4j "1.7.2"]
                 [org.slf4j/log4j-over-slf4j "1.7.2"]
                 
                 ;; Service dependencies
                 [org.languagetool/language-en "2.1"]
                 
                 ;; Clojurescript
                 [prismatic/dommy "0.1.0"]
                 [cljs-ajax "0.1.0"]
                 ;;[domina "1.0.1" :exclusions [org.clojure/clojure]]
                 [jayq "2.3.0"]]
  :profiles {:dev {:source-paths ["src/dev"]
                   :dependencies [[sablecc/sablecc "3.7"]]}}
  :min-lein-version "2.0.0"
  :resource-paths ["config" "resources" "src/java"] ;;tbd: clean this up. move lexer.dat to resources
  :plugins [[lein-cljsbuild "0.3.0"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src/cljs"], 
              :notify-command ["growlnotify" "-m" ""]
              :compiler {:pretty-print true, 
                         :output-to "resources/public/cljs/main.js",
                         :output-dir "resources/public/cljs/"
                         :optimizations :none}}
             {:id "prod"
              :source-paths ["src/cljs"], 
              :compiler {:output-to "resources/public/js/main.js",
                         :optimizations :advanced
                         :externs ["externs/codemirror.js" "externs/jquery-1.9.js" "externs/bootstrap.js"]}}]}
  :main ^{:skip-aot true} latex-grammar-check.server
  :repositories {"3rd party" "https://github.com/alexguev/maven/raw/master/releases"})
