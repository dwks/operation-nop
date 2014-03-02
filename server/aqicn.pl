#!/usr/bin/perl

my @data = `curl --silent $ARGV[0]`;

for my $line (@data) {
    if($line =~ m|<div\s+class='saqi item'[^>]+>(\d+)</div>|) {
        print "$1\n";
    }
}
