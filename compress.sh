contents=("WebCollectives"/*)
parallel tar -cpzf "WebCollectives.tar.gz" "WebCollectives/{}" ::: "${contents[@]##*/}"
