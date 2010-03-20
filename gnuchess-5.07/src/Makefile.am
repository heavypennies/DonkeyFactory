## Makefile.am for gnuchess. Process with automake to make Makefile.in

bin_SCRIPTS = gnuchessx

bin_PROGRAMS = gnuchess

gnuchess_SOURCES = atak.c book.c cmd.c debug.c epd.c eval.c genmove.c \
 getopt.c getopt1.c hash.c\
 hung.c	init.c input.c iterate.c main.c move.c null.c output.c players.c\
 pgn.c ponder.c quiesce.c random.c repeat.c search.c solve.c sort.c\
 swap.c test.c ttable.c util.c common.h book.h eval.h getopt.h \
 inlines.h version.h lexpgn.l lexpgn.h

AM_CFLAGS = $(PTHREAD_CFLAGS)

AM_LDFLAGS = $(PTHREAD_LDFLAGS) $(PTHREAD_LIBS)

DISTCLEANFILES = *~
