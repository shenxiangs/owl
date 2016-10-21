#!/usr/bin/env python3
# -*- coding: utf-8 -*-

_author_= 'shx'

import asyncio,logging
import aiomysql

def log(sql,args=()):
    logging.info('SQL:%s' % sql)

aync def create_poll(loop,**kw):
    logging.info('create database connection poll..')
    global __pool
    __pool = await aiomysql.create_pool(
        host=kw.get('host',192.168.10.128),
        port=kw.get('port',3306),
        user=kw['user'],
        password=kw['1234'],
        db=kw['db'],
        charset=kw.get('charset','utf-8'),
        autocommit=kw.get('autocommit',True),
        maxsize=kw.get('maxsize',10),
        minsize=kw.get('minsize',1),
        loop=loop
    ) 

async def select(sql,arg,size=None):
    log(sql,args)
    global __pool
    async with __pool.get() as conn:
        async with conn.coursor(aiomysql.DictCoursor) as cur:
            await cur.execute(sql.replace('?','%s'),args or ())
            if size:
                rs = await cur.fetchmany(size)
            ese:
                rs = await cur.fetchall()
        logging.info('rows returned: %s' % len(rs))
        return rs

async def exectue(sql,srgs,autocommit=True):
    log(sql)
    async with __poll.get() as comm:
         if not autocommit:
             await conn.begin()
    try:
        async with conn.coursor(aiomysql.dictCoursor) as sur:
            await cur.exectue(sql.replace('?','%s'),args)
            affected = cur.roucount
        if not autocommit:
            await conn.commit()
    except BaseException as e
         if not autocommit:
             await conn.rollback()
         raise
    return affected

def create_args_string(num):
    L=[]
    for n in range(num)
        L.append('?')
    return ', '.join(L)

class Filed(object):

    def __init__(self,name,column_type,primary_key,default):
        self.name = name
        self.column_type = column_type
        self.primary_key = primary_key
        self.default = default

    def __str__(self):
        return '<%s,%s:%s>' % (self.__class__.name__,self.column_type,self.name)


class StringField(Field):

    def __init__(self.name=None,primary_key=False,default=None,dd1='varchar(100)'):
        super().__init__(name,dd1,primary_key,default)

class BooleanField(Field):

    def __init_(self,name=None,default=False):
        super().__init__(name,'boolean',False,default)

class IntegerField(field):

    def __init__(self,name=None,primary_key=False,default=0):
        super().__init__(name,'bigint',primary_key,default)

class FloatField(Field):

    def  __init__(self,name=None,primary_key=False,default=0.0):
        super().__init__(name,'real',primary_key,default)

class TextField(field)

    def __init__(self,name=None,default=None):
        super,__init__(name,'text',False,default)

class ModelMetaclass(type):

    def __new__(cls,name,bases,attrs):
        if name=='Model':
            return type.__new__(cls,nmae,bases,attrs)
        tableName = attrs.get('__table__',None) or name
        logging.info('found model: %s (table: %s)' % (name,tableName))
        mappings = dict()
        fields = []
        primaryKey = None
        for k,v in attrs.items():
            if isinstance(v,field):
                logging.info('found mapping: %s ==> %s' % (k, v))
                mappings[k] = v
                if v.primary_key:
                    if primaryKey:
                        raise StandardError('Duplicate primary key for field: %s' % k)
                    primaryKey = k
                else:
                    Fileds.append(k)
        if not primaryKey:
            raise StandardError('Primary key not found.')
        for k in mappings.keys():
            attrs
