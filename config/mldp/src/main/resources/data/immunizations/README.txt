/bin/rm -f concepts.txt
sort -t\, -k 1,1 -k 4,4 *terminology.csv | \
 perl -ne 'chop; @_=split/,/; if ($_[3] eq "pt") { print "\n$_[0]|$_[1]|$_[2]|"; } else {print "$_[2]|";}' > concepts.txt
