#pragma once
#include "pch.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <conio.h>
#include <list>

class Klient {
public:
	std::string nick;
	std::string login;
	std::string password;
	bool logged_in;
	int socket;
	std::list<Klient*> friends;
	Klient();
	Klient(std::string nick, std::string login, std::string password,
		bool logged_in = false, int socket = -1);
};