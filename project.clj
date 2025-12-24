(defproject echo-graalvm "0.1.0-SNAPSHOT"

  :description "A simple telegram echo bot to experiment with GraalVM native-image and Clojure"

  :url "https://github.com/macielti/echo-graalvm"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.12.4"]
                 [net.clojars.macielti/http-client-component "1.2.1"]
                 [net.clojars.macielti/telegrama "0.2.0"]
                 [com.taoensso/timbre "6.8.0"]
                 [com.github.clj-easy/graal-build-time "1.0.5"]]

  :profiles {:dev {:plugins      [[lein-shell "0.5.0"]
                                  [com.github.liquidz/antq "RELEASE"]
                                  [com.github.clojure-lsp/lein-clojure-lsp "2.0.13"]]

                   :dependencies [[hashp "0.2.2"]]

                   :injections   [(require 'hashp.core)]

                   :aliases      {"clean-ns"     ["clojure-lsp" "clean-ns" "--dry"] ;; check if namespaces are clean
                                  "format"       ["clojure-lsp" "format" "--dry"] ;; check if namespaces are formatted
                                  "diagnostics"  ["clojure-lsp" "diagnostics"] ;; check if project has any diagnostics (clj-kondo findings)
                                  "lint"         ["do" ["clean-ns"] ["format"] ["diagnostics"]] ;; check all above
                                  "clean-ns-fix" ["clojure-lsp" "clean-ns"] ;; Fix namespaces not clean
                                  "format-fix"   ["clojure-lsp" "format"] ;; Fix namespaces not formatted
                                  "lint-fix"     ["do" ["clean-ns-fix"] ["format-fix"]] ;; Fix both

                                  "native"       ["shell"
                                                  "native-image"
                                                  "--no-fallback"
                                                  "--enable-url-protocols=http,https"
                                                  "-march=compatibility"
                                                  "--report-unsupported-elements-at-runtime"

                                                  "--initialize-at-build-time"

                                                  ;;prometheus
                                                  "--initialize-at-run-time=io.prometheus.client.Striped64"

                                                  "--features=clj_easy.graal_build_time.InitClojureClasses"
                                                  "-jar" "./target/${:uberjar-name:-${:name}-${:version}-standalone.jar}"
                                                  "-H:+UnlockExperimentalVMOptions"
                                                  "-H:+StaticExecutableWithDynamicLibC"
                                                  "-H:Name=./target/${:name}"]}}}
  :main echo-graalvm.components)
