package de.spraener.nxtgen.symfony.php;

import de.spraener.nxtgen.target.NonEmptyPrefixedListSection;

public class PhpImplementsSection extends NonEmptyPrefixedListSection {
    public PhpImplementsSection() {
        super("php-implements", "implements ", ", ");
    }
}
