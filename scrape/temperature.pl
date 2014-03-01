#!/usr/bin/perl

my $url = 'http://www20.statcan.gc.ca/tables-tableaux/cansim/csv/01530103-eng.zip';
my $counter = 0;

use File::Temp qw(tempfile);
($fh, $filename) = tempfile();

system("wget --quiet -O $filename $url");
my @data = `unzip -p $filename`;
unlink $filename;

print "ID|TIME|RES2|RES3|VALUE\n";

for my $line (@data) {
    $line =~ s/\s+$//;
    my ($year, $region, $variable, $season, $vector, $coordinate, $value) = split ',', $line;
    next if $year eq 'Ref_Date';  # skip header

    #print "[$line]\n";

    if($variable =~ /Temperature/) {
        print join '|', ($counter, $year, $region, $season, $value);
        print "\n";
    }
    #print "$counter $region $variable $coordinate $value\n";
    $counter ++;
}
