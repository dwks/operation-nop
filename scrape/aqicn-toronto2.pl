#!/usr/bin/perl

my @data = `curl --silent http://aqicn.org/city/canada/ontario/toronto-downtown/m/`;

for my $line (@data) {
    if($line =~ m|var city = "([^"]+)"|) {
        print "In [$1], ";
    }
    if($line =~ m|<div\s+class='saqi item'[^>]+>(\d+)</div>|) {
        print "the air quality is $1\n";
    }
}
