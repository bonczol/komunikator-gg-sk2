#pragma once
#include <string>
#include <ctime>
#include <list>
#include "Klient.h"

class Message{
private:
	std::string msg;
	std::string date;
	std::string time;
	Klient* sender;

	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & msg;
		ar & date;
		ar & time;
		ar & sender;
	}

public:
	Message(std::string msg, Klient* sender);
	string getMsg() { return this->msg; }
	string toString1(int id_conv);
	string toString2();
	~Message();
};

