#!/usr/bin/perl

my @data = `curl --silent http://aqicn.org/city/canada/ontario/toronto-downtown/`;

for my $line (@data) {
    if($line =~ m|title='([^']+)'\s+class='aqihreftarget'|) {
        print "In [$1], ";
    }
    if($line =~ m|<div\s+class='aqivalue'[^>]+>(\d+)</div>|) {
        print "the air quality is $1\n";
    }
}
