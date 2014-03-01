#!/usr/bin/perl

my $url = 'http://www20.statcan.gc.ca/tables-tableaux/cansim/csv/01530103-eng.zip';
my $counter = 0;

use File::Temp qw(tempfile);
($fh, $filename) = tempfile();

system("wget --quiet -O $filename $url");
my @data = `unzip -p $filename`;
unlink $filename;

for my $line (@data) {
    $line =~ s/\s+$//;
    my ($year, $region, $variable, $frequency, $vector, $coordinate, $value) = split ',', $line;
    next if $year eq 'Ref_Date';  # skip header

    #print "[$line]\n";
    print "$counter $region $variable $coordinate $value\n";
    $counter ++;
}
