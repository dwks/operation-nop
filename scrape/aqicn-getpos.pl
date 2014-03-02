#!/usr/bin/perl

my $url = $ARGV[0];
my $city = $ARGV[1];

my @data = `curl --silent $url`;
for my $line (@data) {
    if($line =~ /var mapCityData.*$city","x":\d+,"g":\["([-\d.]+)","([-\d.]+)"/) {
        print "$1 $2\n";
    }
}
