#!/usr/bin/perl
print "ID|RES2|DESC|POS_X|POS_Y\n";
my $c = 0;
while(<>) {
    if(/(\S+) (.+)/) {
        my $url = $1;
        my $city = $2;
        my $pos = `./aqicn-getpos.pl '$url' '$city'`;
        $pos =~ /(\S+) (\S+)/;
        print "$c|$url|$city|$1|$2\n";
    }
    $c ++;
}
